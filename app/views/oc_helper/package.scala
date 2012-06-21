package views.html;

/**
 * Contains template helpers, for example for generating HTML forms.
 */
package object oc_helper {

    import play.api.Logger
    import play.api.templates._
    import play.api.data._
    import scala.collection.JavaConverters._
    import controllers.ViewType
    import java.lang.Boolean
    import scala.xml._
    import views.html.helper._

  def disabledOrNot()(implicit viewType: ViewType) = {
    if (viewType == ViewType.view) 'disabled -> "disabled" else '_dummy -> null
  }

  def crudFormTarget(crudBaseRoute: String)(implicit viewType: ViewType) = {
    crudBaseRoute + (viewType match {
        case ViewType.view => "/update"
        case ViewType.create => "/create"
        case ViewType.update => "/update"
    })
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

  def multiselect[T](field: play.api.data.Field,
    options: Seq[T],
    optionValue: T => String,
    optionText: T => String,
    fieldValue: play.api.data.Field => Option[String],
    args: (Symbol, Any)*)(implicit handler: FieldConstructor, lang: play.api.i18n.Lang) = {
    val values = field.indexes.map { v => fieldValue(field("[" + v + "]")).get }
    val disabled = args.exists(_._1=='disabled)
    input(field, args: _*) {
      (id, name, value, htmlArgs) =>
        Html(
          (  
            <div>
        	   { 
        	     if (!disabled) {
        		   <input type="button" class="select-all" id={id + "-select-all"} value="alle"/>
        		   <input type="button" class="select-none" id={id + "-select-none"} value="keinen"/>
        		   <br/>
        	     } 
        	   }
        	   { addAttributes(
		            <select id={ id } name={ name } multiple="multiple">
	              {
		                options.map { v =>
		                  val value = optionValue(v)
		                  val z: Option[String] = if (values contains value) Some("selected") else None
		                  <option value={ value } selected={ z map Text }>{ optionText(v) }</option>
		                }
		              }
                </select>,
	            htmlArgs) }
        	   {
        	     if (!disabled) {
        	    	 <script type="text/javascript">
        	    	 $('#{id}-select-all').click(function() {{
        	    	 	$('#{id} option').each(function(){{
        	    	 		$(this).attr('selected', true);
        	    	 	}});
        	    	 }});
        	    	 $('#{id}-select-none').click(function() {{
        	    	 	$('#{id} option').each(function(){{
        	    	 		$(this).removeAttr('selected');
        	    	 		// twice => workaround for strange bug in chrome?
        	    	 		$(this).removeAttr('selected');
        	    	 	}});
        	    	 }});
        	    	 </script>
        	     }
        	   }
            </div>
            
          ).toString)
            
    }
  }

}