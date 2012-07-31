package views.html.oc_helper

import views.html.helper._

/**
 * Contains template helpers, for example for generating HTML forms.
 */
package object bootstrap2 {
    
  implicit val bootstrap2Field = new FieldConstructor {
    def apply(elts: FieldElements) = bootstrap2FieldConstructor(elts)
  }

}