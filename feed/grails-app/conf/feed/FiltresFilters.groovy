package feed

class FiltresFilters {

    def filters = {
        all(controller:'*', action:'*') {
            before = {
      println "ouesch mec"
	  println controllerName
	  println actionName
            }
            after = { Map model ->

            }
            afterView = { Exception e ->

            }
        }
    }
}
