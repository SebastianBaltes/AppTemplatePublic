package views.html.oc_helper

import views.html.helper._

/**
 * Contains template helpers, for example for generating HTML forms.
 */
package object hidden {
    
  implicit val hiddenField = new FieldConstructor {
    def apply(elts: FieldElements) = hiddenFieldConstructor(elts)
  }

}