def label = "worker-${UUID.randomUUID().toString()}"

podTemplate(label: label, containers: [ 
  containerTemplate(name: 'maven', image: 'maven:3.6.0-jdk-8-alpine', command: 'cat', ttyEnabled: true,
  envVars: [envVar(key: 'MAVEN_CONFIG', value: '/home/jenkins/.m2')]),
  containerTemplate(name: 'gradle', image: 'gradle:4.5.1-jdk9', command: 'cat', ttyEnabled: true)
  ],
  volumes: [
      configMapVolume(configMapName: 'settings-xml', mountPath: '/home/jenkins/.m2'),
      hostPathVolume(mountPath: '/home/gradle/.gradle', hostPath: '/tmp/jenkins/.gradle')
  ]
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
    
    stage('Build Code') {
       container('gradle') {
          unstash "code-stash"
          sh """
            pwd
            ls -lat
            gradle build -g gradle-user-home
            ls -lat
            """
       }
    }   

    stage('Run maven') {
      container('maven') {
       unstash "code-stash"
       sh "cat /home/jenkins/.m2/settings.xml"
       sh "mvn --version"
      // sh "curl -k https://repo.maven.apache.org/maven2" 
      // sh "mvn -B clean install -X"
      }
    }
   
  }
}
