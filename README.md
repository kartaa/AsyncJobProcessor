README
======

Async Job Processor.

The project aims to build a generic job processor that can take a job (in an extensible form) as its input and 
provide for async job processing.

Also, we am aim to provide the state of the art bells and whistles so that one can start just get on with their 
job processing the moment they sync down.


Usage
=====

One can build up their own customized job server starting with the code provided here.

Here are some commands to be executed from project's home folder that are useful during this project's operation.

mvn clean package - 	This will create a deployable war file in target directory.

mvn jboss-as:deploy - 	This will deploy the war file for the project from target directory to a locally running instance of jboss server.

[![Build Status](https://travis-ci.org/kartaa/AsyncJobProcessor.svg?branch=master)](https://travis-ci.org/kartaa/AsyncJobProcessor)
