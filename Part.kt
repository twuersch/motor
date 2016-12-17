import org.jsoup.nodes.Node

abstract class Part (
  var x: Int = 0,
  var y: Int = 0,
  var width: Int = 0,
  var height: Int = 0,
  var topPadding: Int = 5,
  var rightPadding: Int = 5,
  var bottomPadding: Int = 5,
  var leftPadding: Int = 5,
  var children: MutableList<Part> = mutableListOf()
)

