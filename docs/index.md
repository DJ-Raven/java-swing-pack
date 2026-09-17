# Swing Pack Guide

**Swing Pack** is a modern Swing component library for Java desktop development.
It provides beautiful, customizable, and ready-to-use UI components with a flat, modern design build with **FlatLaf**.

## Installation

[![Maven Central](https://img.shields.io/maven-central/v/io.github.dj-raven/swing-pack?label=Maven%20Central)](https://central.sonatype.com/artifact/io.github.dj-raven/swing-pack)

Add the dependency:

``` xml
<dependency>
    <groupId>io.github.dj-raven</groupId>
    <artifactId>swing-pack</artifactId>
    <version>{version}</version>
</dependency>
```

### Snapshots

To get the latest updates before the release, you can use the snapshot version
from [Sonatype Central](https://central.sonatype.com/service/rest/repository/browse/maven-snapshots/io/github/dj-raven/swing-pack/)

``` xml
<repositories>
    <repository>
        <name>Central Portal Snapshots</name>
        <id>central-portal-snapshots</id>
        <url>https://central.sonatype.com/repository/maven-snapshots/</url>
    </repository>
</repositories>
```

Add the snapshot version:

``` xml
<dependency>
    <groupId>io.github.dj-raven</groupId>
    <artifactId>swing-pack</artifactId>
    <version>{version}-SNAPSHOT</version>
</dependency>
```

## Components

| Component | Description |
|---|---|
| [Pagination](components/pagination.md) | Navigate through multiple pages of content. |
| [Multi-Select ComboBox](components/multi-select-combobox.md) | A combo box that supports selecting multiple items. |
| [DateTime Field](components/datetime-field.md) | An input field for picking dates and times. |