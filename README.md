# Onion Routing
_Advanced Internet Computing - Group 4_

## Team members
 * Lisa Fichtinger
 * [Manuel Geier](http://geier.io)
 * Markus Zisser
 * Mihai Lepadat
 * [Thomas Rieder](http://rieder.io)

## Getting started

First of all you need to install ``activator`` and its dependencies. Check the website of the Play Framework for details (https://www.playframework.com/download). After that you can either run or build the appliations.

To **run** them, change into the corresponding directory and execute ``activator run``. This will start the applicalication on the default port 9000. This might be good for taking a look at a single application, but is suboptimal for examining the whole system.

To **build** them, change into the directory and execute ``activator dist`` to generate a binary. Alternatively you may use the ``build.sh`` script. This script will generate binaries for all services and store them in a ``build/`` folder. After building you need to extract the ZIP files and start the applications like this:
```
# name is one of the following: node, originator, quote, directory
bin/$name -Dconfig.file=conf/application.conf
```

If you want to keep them running after the shell is close, do something like:
```
nohup bin/originator -Dconfig.file=conf/application &
``` 

Note: The directory service requires some additional work before it can be started:
``
mkdir provision-logs
``

## Deployment

For the deployment you will need to install Ansible (http://www.ansible.com/home).

### Creating new chain nodes
The ``provision.yml`` playbook can be used to instantiate new chain nodes as well as ensure that existing chain nodes are running.

It ensures the following:

 * there are #N chain nodes running at EC2 (identified by name)
   * this means that it also _stops_ instances
 * nginx is configured
 * the chain node process is running
 * the process uses the right config file

**This playbook assumes that there is a ``node-binary.zip`` file in the same directory. This file should be created by using ``activator dist`` for the chain-node.**

It also assumes that the following environment variables are set:
 
 * ``AWS_SECRET_ACCESS_KEY``
 * ``AWS_ACCESS_KEY_ID``


## Used technologies
 * Java
 * Play Framework
 * REST APIs
 * Ansible
