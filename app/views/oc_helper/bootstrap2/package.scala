package views.html.oc_helper


/**
 * Contains template helpers, for example for generating HTML forms.
 */
package object bootstrap2 {

  import views.html.helper._
    
  implicit val bootstrap2Field = new FieldConstructor {
    def apply(elts: FieldElements) = bootstrap2FieldConstructor(elts)
  }

}