package views.html.oc_helper

import views.html.helper.FieldConstructor
import views.html.helper.FieldElements

/**
 * Contains template helpers, for example for generating HTML forms.
 */
package object noLabels {

    
  implicit val noLabelsField = new FieldConstructor {
    def apply(elts: FieldElements) = noLabelsFieldConstructor(elts)
  }

}