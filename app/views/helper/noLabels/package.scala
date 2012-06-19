package views.html.helper

/**
 * Contains template helpers, for example for generating HTML forms.
 */
package object noLabels {

  implicit val noLabelsField = new FieldConstructor {
    def apply(elts: FieldElements) = noLabelsFieldConstructor(elts)
  }

}