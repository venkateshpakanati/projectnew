def label = "worker-${UUID.randomUUID().toString()}"

podTemplate(label: label, containers: [ 
  containerTemplate(name: 'maven', image: 'maven:3.6.0-jdk-8-alpine', command: 'cat', ttyEnabled: true,
  envVars: [envVar(key: 'MAVEN_CONFIG', value: '/home/jenkins/.m2')]),
  containerTemplate(name: 'docker', image: 'trion/jenkins-docker-client'),
//  containerTemplate(name: 'kubectl', image: 'lachlanevenson/k8s-kubectl:v1.8.8', command: 'cat', ttyEnabled: true),
//  containerTemplate(name: 'helm', image: 'lachlanevenson/k8s-helm:latest', command: 'cat', ttyEnabled: true)

  ],
  volumes: [
      configMapVolume(configMapName: 'settings-xml', mountPath: '/home/jenkins/.m2'),
      hostPathVolume(mountPath: '/var/run/docker.sock', hostPath: '/var/run/docker.sock')
  ]
) {
  node(label) {
    def app
    stage('Checkout Code') {
       milestone ()
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
    
    stage('Build maven project') {
      milestone ()
      container('maven') {
       unstash "code-stash"
      // sh "cat /home/jenkins/.m2/settings.xml"
      // sh "mvn --version"
       sh "mvn -V -B -U -T 8 clean install -s /home/jenkins/.m2/settings.xml"
     //  sh "mvn -B clean install -X"
       sh "ls -lrt"
       sh "cd target && ls -lrt"
   //    stash name: "jar-stash", includes: "target/CacheProject-1.0.0.jar"
      }
    }

    stage('Build docker image and publish') {
       milestone ()
      
       container('docker') {
     //    unstash "jar-stash"
        //  sh '''
        //    ls -lrt
        //    docker version
        //    docker build . -t cache-demo
        //    docker images
        //  '''
          // app = docker.build("cache-demo")
          // docker.withRegistry('https://registry.hub.docker.com', 'docker-hub-credentials') {
          //   app.push("${env.BUILD_NUMBER}")
          //   app.push("latest")
          // }

          docker.withRegistry('https://registry.hub.docker.com', 'docker-hub-credentials') {
            def customImage = docker.build("venkateshpakanati/cache-demo:${env.BUILD_ID}")
           /* Push the container to the custom Registry */
            customImage.push()
          }
       }
     
    }

    // stage("Deploy") {
    //   milestone()
    //   sh "ls -lrt"
    //   // script {
    //   //     kubernetesDeploy(configs: "deployment.yaml", kubeconfigId: "kubeconfig")
    //   // }
    // }

    // stage('Run kubectl') {
    //   container('kubectl') {
    //     sh "kubectl get pods"
    //   }
    // }
    stage('Run helm') {
      milestone()
      container('helm') {
        sh "ls -lrt"
       // sh "helm list"
        sh "helm install --dry-run --debug ./projectchart"
      }
    }
   
  }
}
