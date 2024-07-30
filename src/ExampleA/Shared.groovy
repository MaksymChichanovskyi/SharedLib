package ExampleA

    
    def defaultCheckout() {
        checkout(scm)
    }

    def startBuild(String imageName = "maven:3.9.8-amazoncorretto-11") {
        docker.image(imageName).pull()
        docker.image(imageName).inside() {
            sh "mvn clean package"
        }
    }

   def updatePOMVersion(String basePath, String fileName = 'pom.xml', def parentModel = null, def modelMap = null, String customVersion) {
    modelMap = modelMap ?: [:]
    def filePath = "${basePath}/${fileName}"
    def mavenModel = readMavenPom(file: filePath)
    def moduleKey = "${getModelGroupId(mavenModel)}:${getModelArtifactId(mavenModel)}"
    echo "Updating version for ${moduleKey} (${filePath})"
    modelMap[moduleKey] = [model: mavenModel, path: filePath]
    String version = mavenModel.getVersion()
    if (version) {
        echo "Version info found. Bumping to ${customVersion}"
        mavenModel.setVersion(customVersion)
    } else if (!parentModel) {
        error "Detected root pom.xml with inherited version\nThis format is not supported\nPlease add the version tag to your root pom.xml"
    } else {
        echo "Version info not found, maven will use inherited version"
    }
    if (parentModel) {
        echo "Model has parent"
        def parent = mavenModel.getParent()
        if (!mavenModel.getGroupId() || parent.getGroupId() == mavenModel.getGroupId()) {
            echo "Updating parent info for this model"
            parent.setVersion(parentModel.getVersion())
            mavenModel.setParent(parent)
        } else {
            echo "Foreign parent. Do nothing"
        }
    }
    List<String> modules = mavenModel.getModules()
    if (modules) {
        echo "Updating modules"
        for (String moduleName in modules) {
            echo "Updating ${moduleName}"
            updatePOMVersion("${basePath}/${moduleName}", fileName, mavenModel, modelMap, customVersion)
        }
    }
    return modelMap
}

    def mavenApp(){
        def agentName = 'linux && docker'
        def someText = 'Hello!'
         

node(agentName) { 
    stage('Checkout') {
        defaultCheckout()
    }
     stage('Update Version'){
      updatePOMVersion()
     }
    
  stage('Build'){
      startBuild()
    }
}

    }

return this
