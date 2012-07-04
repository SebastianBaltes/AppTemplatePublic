package views.html;

import play.api.Logger
import play.api.templates._
import play.api.data._
import scala.collection.JavaConverters._
import controllers._
import java.lang.Boolean
import scala.xml._
import views.html.helper._
import models.CrudModel
import play.data.format.Formatters
import global.Html5DateFormatter
import global.TimeStampFormatter
import org.apache.commons.lang.StringUtils

/**
 * Contains template helpers, for example for generating HTML forms.
 */
package object oc_helper {

  def notEmptyOrElse(field: play.api.data.Field, defaultValue: String) = {
    val v = field.value.getOrElse(null)
    if (StringUtils.isEmpty(v)) {
      defaultValue
    } else {
      v
    }
  }
  
  def fmt(value: Any)(implicit lang: play.api.i18n.Lang): Html = {
	 value match {
	   case x:java.sql.Date => Html(Html5DateFormatter.formatLocale(x, lang.toLocale))
	   case x:java.util.Date => Html(TimeStampFormatter.formatLocale(x, lang.toLocale))
	   case x:Boolean => Html("<i class='"+(if (x) "icon-ok" else "icon-minus")+"' title='"+Formatters.print(value)+"'></i>")
	   case _ => Html(Formatters.print(value)) 
	 }
  }
  
  // FIXME bessere :? Syntax??
  def elvis[T](obj: T, f: T => Any, ifNullValue: Any = "") = {
    if (obj==null) {
      ifNullValue
    } else {
      f(obj)
    }
  }
  
  def disabledOrNot()(implicit viewType: ViewType) = {
    if (viewType == ViewType.view) 'disabled -> "disabled" else '_dummy -> null
  }

  def crudFormTarget(crudBaseRoute: String)(implicit viewType: ViewType) = {
    crudBaseRoute + (viewType match {
      case ViewType.view => "/view"
      case ViewType.create => "/save"
      case ViewType.update => "/update"
    })
  }

  def link(newPage: Int, newSortBy: String)(implicit clist: CrudListState): String = {
    var sortBy = clist.currentSortBy
    var order = clist.currentOrder
    if (newSortBy != null) {
      sortBy = newSortBy
      if (clist.currentSortBy == newSortBy) {
        if (clist.currentOrder == "asc") {
          order = "desc"
        } else {
          order = "asc"
        }
      } else {
        order = "asc"
      }
    }
    clist.crudBaseUrl + "/list?newPage=" + newPage + "&sortBy=" + sortBy + "&order=" + order + "&filter=" + clist.currentFilter + "&rowsToShow=" + clist.rowsToShow
  }

  def sortByCss(key: String)(implicit clist: CrudListState): String = {
    if (clist.currentSortBy == key) {
      if (clist.currentOrder == "asc") "sorting_asc" else "sorting_desc"
    } else {
      "sorting"
    }
  }

  def header(key: String, title: String)(implicit clist: CrudListState): Html = {
    new Html("<th class='" + sortByCss(key) + "'> <a href='" + link(0, key) + "'>" + title + "</a> </th>")
  }

  def asBoolean(field: Field): Boolean = {
    field.value.getOrElse("false").toBoolean
  }

  def asLong(field: Field): Long = {
    field.value.getOrElse(null).toLong
  }

  def addAttributes(element: Elem, attributes: Map[Symbol, _ <: Any]): Elem = {
    var el = element
    for ((key, value) <- attributes) el = el % Attribute(None, key.name, Text(value.toString), Null)
    el
  }

  val selectDefaultOptionValue = (x: Any) => {
    x match {
      case c: CrudModel[_] => c.getId.toString
      case _ => x.toString
    }
  }

  val selectDefaultOptionText = (x: Any) => {
    x match {
      case c: CrudModel[_] => c.label
      case _ => x.toString
    }
  }

  val selectDefaultValue = (f: play.api.data.Field) => {
	val id = f("id").value
    if (id.isDefined) {
      id
    } else {
      f.value
    }
  }

  def singleselect[T](
    field: play.api.data.Field,
    options: Seq[T],
    optionValue: T => String = selectDefaultOptionValue,
    optionText: T => String = selectDefaultOptionText,
    fieldValue: play.api.data.Field => Option[String] = selectDefaultValue)(args: (Symbol, Any)*)(implicit handler: FieldConstructor, lang: play.api.i18n.Lang) = {

    val values = Set(fieldValue(field).getOrElse(null))
    val disabled = args.exists(_._1 == 'disabled)
    input(field, args: _*) {
      (id, name, value, htmlArgs) =>
        Html(
          (
            <div>
              {
                addAttributes(
                  <select id={ id } name={ name } size="1">
                    {
                      options.map { v =>
                        val value = optionValue(v)
                        val z: Option[String] = if (values contains value) Some("selected") else None
                        <option value={ value } selected={ z map Text }>{ optionText(v) }</option>
                      }
                    }
                  </select>,
                  htmlArgs)
              }
            </div>).toString)

    }
  }

  def multiselect[T](
    field: play.api.data.Field,
    options: Seq[T],
    optionValue: T => String = selectDefaultOptionValue,
    optionText: T => String = selectDefaultOptionText,
    fieldValue: play.api.data.Field => Option[String] = selectDefaultValue)(args: (Symbol, Any)*)(implicit handler: FieldConstructor, lang: play.api.i18n.Lang) = {
    
    Logger.info("~~~~~~ "+field);
    
    field.indexes.foreach( x => { Logger.info("######## "+x) } );
    
    val values = field.indexes.map { v => fieldValue(field("[" + v + "]")).get }
    val disabled = args.exists(_._1 == 'disabled)
    input(field, args: _*) {
      (id, name, value, htmlArgs) =>
        val mid = id + "-multiselect"
        if (disabled) {
          Html(
            (
	        addAttributes(
	          <select id={ id } name={ name } multiple="multiple">
                {
                  options.filter(values contains optionValue(_)).map { v => <option value={ optionValue(v) }>{ optionText(v) }</option> } 
                }
              </select>,
            htmlArgs)          
          ).toString)
        } else {
          Html(
            (
              <div>
        		  <div id={mid} style="width:400px">
        		  </div>
        		  <script type="text/javascript">
        		  $(function() {{
        		    var t = $('#{mid}').bootstrapTransfer({{
        		      'target_name': '{ name }[#].id',
        		      'height': '12em',
        		      'hilite_selection': false
        		    }});
        		    t.populate([
        		      {
        		        options.map { v =>
	                        "{value:'"+optionValue(v)+"', content:'"+optionText(v)+"'},"
        		        } 
        		      }
        		    ]);
        		    // { field.indexes }
        		    t.set_values([
        		      {
        		        options.filter(values contains optionValue(_)).map { v =>
	                        "'"+optionValue(v)+"',"
        		        } 
        		      }
        		    ]);
          		    //t.set_values(["2", "4"]);
        		    //console.log(t.get_values());
        		  }});
        		  </script>
              </div>
             ).toString)
        }
    }
  }

}