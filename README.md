Contacts Service
================

Sample code for contacts web service assignment.
The files here are:
*  src/contact/entity/Contact.java       contact entity needed by DAO and test.
*  src/contact/service/ContactDao.java   interface for a ContactDao that provides persistence services
*  src/contact/service/DaoFactory.java   for getting a ContactDao object as singleton
*  src/contact/service/mem/*             implementation of ContactDao and DaoFactory using Lists to store contacts
*  src/contact/service/jpa/*             implementation of ContactDao and DaoFactory using JPA
*  test/contact/service/ContactDaoTest.java  JUnit 4 test for ContactDao
*  src/META-INF/persistence.xml          configuration file for JPA
*  lib/*                                 JPA libraries and Derby database

These are examples. You can use, not use, or modify them.

Refer to an [issue](#3).
