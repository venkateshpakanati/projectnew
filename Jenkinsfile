def label = "worker-${UUID.randomUUID().toString()}"

podTemplate(label: label, containers: [ 
  containerTemplate(name: 'maven', image: 'maven:3.6.0-jdk-8-alpine', command: 'cat', ttyEnabled: true,
  envVars: [envVar(key: 'MAVEN_CONFIG', value: '/home/jenkins/.m2')]),
  containerTemplate(name: 'docker', image: 'trion/jenkins-docker-client'),
//  containerTemplate(name: 'kubectl', image: 'lachlanevenson/k8s-kubectl:v1.8.8', command: 'cat', ttyEnabled: true),
  containerTemplate(name: 'helm', image: 'lachlanevenson/k8s-helm:latest', command: 'cat', ttyEnabled: true)

  ],
  volumes: [
      configMapVolume(configMapName: 'settings-xml', mountPath: '/home/jenkins/.m2'),
      hostPathVolume(mountPath: '/var/run/docker.sock', hostPath: '/var/run/docker.sock'),
      secretVolume(secretName: 'helm-repository', mountPath: '/home/groot/helm/repository'),
      emptyDirVolume(mountPath: '/home/groot/helm/repository/cache', memory: false)
  ]
) {
  node(label) {
    def app
    def props = null
    boolean isBuildApp = false
    boolean isPublishArtifacts = false
    boolean isDeploy = false
    stage('Checkout Code') {
       milestone ()
       sh """
            git config --global http.proxy ''
            git config --global https.proxy ''
        """
        // git branch: 'master',
        // credentialsId: '35205444-4645-4167-b50e-c65137059f09',
        // url: 'http://13.234.176.102/venkateshpakanati/mymicroservices.git'
        def myRepo = checkout scm
        def gitCommit = myRepo.GIT_COMMIT
        def gitLocalBranch = myRepo.GIT_LOCAL_BRANCH 
        def gitPrevCommit = myRepo.GIT_PREVIOUS_COMMIT
        def gitPrevSuccessCommit = myRepo.GIT_PREVIOUS_SUCCESSFUL_COMMIT
        def gitBranch = myRepo.GIT_BRANCH
        def shortGitCommit = "${gitCommit[0..10]}"
        def previousGitCommit = sh(script: "git rev-parse ${gitCommit}~", returnStdout: true)
        println "${gitCommit}   ${gitBranch}  ${shortGitCommit}  ${previousGitCommit}   ${gitLocalBranch}  ${gitPrevCommit} ${gitPrevSuccessCommit}"
      
        sh "ls -lat"
        stash name: "code-stash", includes: "**/*"
    }
    
    stage('load properties') {
      milestone()
      if (env.BRANCH_NAME == 'develop') {
        props = readProperties file:'jenkins-develop.properties'
      } else {
         props = readProperties file:'jenkins-default.properties'
      }  
     
      isBuildApp = props.build.toBoolean()
      isPublishArtifacts = props.publishartifacts.toBoolean()
      isDeploy = props.deploy.toBoolean()

    }

  if(isBuildApp) {
    stage('Build maven project') {
      milestone ()
      container('maven') {
       unstash "code-stash"
       sh "mvn -V -B -U -T 8 clean install -s /home/jenkins/.m2/settings.xml"
      }
    }
  }

  if(isPublishArtifacts) {
    stage('Build docker image and publish') {
       milestone ()
       container('docker') {
         docker.withRegistry('https://registry.hub.docker.com', 'docker-hub-credentials') {
          def customImage = docker.build("venkateshpakanati/cache-demo:${env.BUILD_ID}")
           customImage.push()
         }
       }
    }
  }  

  if(isDeploy) {
    stage('Run helm') {
      milestone()
      container('helm') {
        sh "ls -lrt"
       // sh "helm list"
       sh "helm repo update --debug"
       // sh "helm upgrade cacheproject projectchart"
        // kubectl create clusterrolebinding serviceaccounts-cluster-admin \
        // --clusterrole=cluster-admin \
        // --group=system:serviceaccounts
      }
    }
  } 
 }
}
