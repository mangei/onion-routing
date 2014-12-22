# Onion Routing
_Advanced Internet Computing - Group 4_

## Team members
 * Lisa Fichtinger
 * [Manuel Geier](http://geier.io)
 * Markus Zisser
 * Mihai Lepadat
 * [Thomas Rieder](http://rieder.io)

## Getting started

TODO put some small guide here

## Deployment

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
