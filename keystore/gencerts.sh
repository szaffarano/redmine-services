#!/bin/bash

make create-keystore PASSWORD=changeit
make add-host HOSTNAME=localhost
make create-truststore PASSWORD=changeit
make add-client CLIENTNAME=dummy CLIENTOU=redmine-one
make add-client CLIENTNAME=dummy2 CLIENTOU=redmine-two
