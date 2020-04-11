def label = "worker-${UUID.randomUUID().toString()}"

podTemplate(label: label, containers: [ 
  containerTemplate(name: 'maven', image: 'maven:3.5-jdk-8', command: 'cat', ttyEnabled: true) 
],
) {
  node(label) {
    
    stage('Checkout Code') {
        // git branch: 'master',
        // credentialsId: '35205444-4645-4167-b50e-c65137059f09',
        // url: 'http://13.234.176.102/venkateshpakanati/mymicroservices.git'
        checkout scm
      
        sh "ls -lat"
        stash name: "code-stash", includes: "**/*"
    }

    stage('Run maven') {
      container('maven') {
       unstash "code-stash"
       sh "mvn --version"
       sh "mvn clean compile"
      }
    }
   
  }
}
