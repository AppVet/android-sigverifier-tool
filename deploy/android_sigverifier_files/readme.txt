Installation of AppVet-compatible tools requires:

- Setting an environment variable for the path to the tool's files. For example,
  on Linux, setting variable in /etc/profile and sudo visudo and "source"ing 
  /etc/profile before starting Tomcat or Eclipse.
- Configuring the tool
- Adding applications, libraries, etc. that comprise the tool to be invoked
- Adding the tool service's war file to Tomcat's /webapps directory or adding 
  the tool service project to Tomcat server in Eclipse
- Adding an AppVet account for the tool service to authenticate and submit reports


For proper operation of the Android MKEF tool, the environment variable 
ANDROID_SIGVERIFIER_FILES_HOME should be globally set to the path for this 
directory. For example:


ANDROID_SIGVERIFIER_FILES_HOME = /home/<username>/android_sigverifier_files




