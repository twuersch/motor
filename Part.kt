import org.jsoup.nodes.Node

abstract class Part (
  var x: Int = 0,
  var y: Int = 0,
  var width: Int = 0,
  var height: Int = 0,
  var topPadding: Int = 0,
  var rightPadding: Int = 0,
  var bottomPadding: Int = 0,
  var leftPadding: Int = 0,
  var children: MutableList<Part> = mutableListOf()
)
