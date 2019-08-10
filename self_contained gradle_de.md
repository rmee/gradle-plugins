# Tooling als Code: das Erstellen isolierter, reproduzierbarer Gradle-Builds

"Works on My Machine" ist ein Klassiker unter Software-Engineering Problemen. Kleine Unterschiede im lokalen Aufbau
von Entwickler Machinen können immer wieder zu Problemen führen.
Das Einrichten und Pflegen eines Projekts und die Isolierung gegenüber anderen Projekten 
kann eine zeitaufwändige Herausforderung sein. Gleiches gilt für 
Unterschiede zwischen Entwicklung, CI/CD und Produktion. Für letzere können Probleme häufig erst spät in der 
Lieferkette erkannt werden und sind entsprechend zeitaufwändig in der Reparatur. In diesem Tutorial präsentieren
wir Werkzeuge um solche Probleme anzugehen und dauerhaft zu lösen auf der Basis eigenständiger, isolierter Builds,
abgeschottet von jeglichen externen Einflüssen.

## Einführung

Es gibt bereits eine breite Palette von Werkzeugen die mit der Einrichtung von Projekten behilflich sind. Zum Beispiel,
mit `nvm` können Benutzer schnell zwischen  Node.js Versionen in der Javascript-Welt wechseln. Dennoch müssen Entwickler
zu jeder Zeit die passende Version wählen. Auf der Java-Seite profitieren Entwickler von der JVM. Diese
 abstrahiert  ein Großteil des zugrunde liegenden Betriebssystems. Typischerweise laufen daher Java-Anwendungen 
gleich gut unter Linux, Windows und OS X. Gradle bringt Projekte einen Schritt weiter mit dem Konzept des
Gradle Wrappers.  Ein Gradle-Build verwendet immer die im Projekte deklarierte Gradle-Version für alle
Build-Operationen. Gradle lädt die deklarierte Version herunter und cached diese für zukünftige Builds.
Damit addressiert Gradle eines der Hauptprobleme von `nvm`: alles geschieht transparent für den Entwickler und das 
Projekt kann selbst über dessen lokalen Setup entscheiden. Man kann dies als *Tooling als Code* bezeichnen, analog 
zu Techniken welche für *Infrastruktur als Code* im Einsatz sind. In den nachfolgenden Abschnitten wird beschrieben wie
solche Techniken nicht nur für Gradle, sondern für alle beteiligten Werkzeugs angewendet werden können, zum Beispiel:

- JDK-Installationen.
- `kubectl` um mit Kubernetes-Clustern zu interagieren.
- `Helm` um Kubernetes-Anwendungen zu paketieren.
- `Terraform` zur Bereitstellung von Infrastruktur.
- `gcloud`, `az`, `oc` für die Arbeit mit Google Cloud, Azure und OpenShift.
- die Installation von Node.js.
- die lokale Ausführung von Datenbanken.
- die Durchführung von UI-Tests mit Cypress.

Es gibt mehrere Projekte die hier behilflich sind:

- https://github.com/rmee/gradle-plugins/ bietet mehrere Gradle-Plugins.
- https://www.testcontainers.org/ um Docker in m Unit-Tests zu verwenden.
- https://github.com/srs/gradle-node-plugin um Node.js in Gradle zu verwenden.
 
Mit https://github.com/crnk-project/crnk-example gibt es eine Beispiel-Applikation welche diverse dieser Techniken
anwendet.


## Installieren einer JVM

Das Plugin `jdk-bootstrap` von https://github.com/rmee/gradle-plugins/ erreicht für die JVM den 
gleichen Komfort wie der Gradle-Wrapper für Gradle. Das Plugin kann als Abhängigkeit definiert:

```
plugins {
    id "com.github.rmee.jdk-bootstrap" version "1.0.20190725142159".
}
```

und zusammen mit dem Gradle-Wrapper verwendet werden:

```
wrapper {
    gradleVersion ='5.5'.
}

Plugin anwenden: jdk-bootstrap'.
jdk {
    useAdoptOpenJdk8('8u202-b08')
}
```

Durch den Anruf von:

```
./gradlew wrapper
```

wird das `gradlew`-Skript mit Logik angereichert um das ausgewählte JDK herunterzuladen, zu cachen und die
`JAVA_HOME` Umgebungsvariable einzurichten. Das Skript sieht wie folgt aus:
 
```
...
JDK_DOWNLOAD_URL="https://github.com/AdoptOpenJDK/openjdk8-binaries/releases/..."
JDK_VERSION="8u202-b08"
JDK_CACHE_DIR="${APP_HOME}/.gradle/jdk"
if [ -z "${JAVA_HOME}" ]; then
	JAVA_HOME="${JDK_CACHE_DIR}/jdk-${JDK_VERSION}";
fi
if ! [ -d "${JAVA_HOME}" ]; then
  mkdir -p "${JDK_CACHE_DIR}" || die "java: Fatal error while creating local cache directory: ${JDK_CACHE_DIR}"
  ...
fi
...
``` 
 
 
Von diesem Zeitpunkt an wird jeder Entwickler die gleiche JVM verwenden! Das
JDK ist im Projekt-lokalen `.gradle` Verzeichnis verfügbar, ebenso wie Gradle selbst. 



## Eigenständiges Werkzeug von der Entwicklung bis zur Produktion

Eine stabile, konsistente JVM- und Gradle-Einrichtung ist ein guter Anfang, aber in der Regel sind diverse weitere Werkzeuge 
über den gesamten Projektlebenszyklus hinweg involviert, insbesondere für das Testing, Provisionierung und Deployment. 
Terraform, Helm und `kubectl` sind typische Werkezuge um Anwendungen auf einem Managed Kubernetes Service wie GKE 
in der Google Cloud zu installieren. Alle diese Werkzeuge stehen als Binärdateien zum Download und Installation
zur Verfügung. Aber dann wiederum stellen sich die obigen Herausforderungen und mehr:

- Die lokale Installation muss vom Entwickler gepflegt werden.
- Binärdateien verschmutzen häufig das Home-Verzeichnis der Entwickler. Sofern Entwickler an mehreren Projekten arbeiten
  kann es schnell zu Überschneidungen führen. So ist es beispielsweise relativ einfach mit `kubectl` in den 
  falschen Kubernetes-Cluster zu deployen. All dies lässt sich mit weiteren Konfigurationen verhindern. Aber typischerweise 
  ist es zu umständlich solche Einstellungen durch den ganzen Entwicklungsablauf hindurch, sowohl 
   innerhalb als auch ausserhalb der Builds, durchzusetzen.
- Binärdateien müssen von einem der Mirror heruntergeladen werden. Obwohl dies für einzelne Entwickler einfach ist,
  kann dies zu erheblichen Problemen im Unternehmensumfeld führen. Es gibt keinen standardisierte, etablierte Mechanismus um
  solche Dateien zu downloaden, cachen und aktualisieren. URLs sind manchmal kryptisch und erschweren eine
  automatische Aktualisierung. Package-Managers wie RPM arbeiten global mit der ganzen Maschine.
- Entwickler, CI/CD und Betreiber verwenden häufig unterschiedliche Werkzeuge.
  Puppet und Ansible finden man häufig für Produktions-Rollouts, während diese aber nur selten lokal
  von Entwicklern genutzt werden. Mit Gradle gibt es bereits eine Task Execution Engine welche während des Entwicklungsprozesses
  verwendet wird. Warum nicht auch in späteren Phasen? Dabei lassen sich die Herausforderungen von oben angehen
  und ein einheitlicher Setup von Entwicklung bis Produktion etablieren.

Der Elefant im Raum ist klar Docker. Dessen Registry ermöglicht den Download, Caching und Aktualisierung von Images.
Container werden in isolierten Laufzeitumgebungen ausgeführt. Jedoch ist es alles andere als klar wie man
am Besten von Docker während der Entwicklung profitieren kann. So gibt es zwar auch Docker Images für 
Terraform, Helm, Kubectl und gcloud, aber nur selten werden diese auch verwendet.

Der einfachste Ansatz, um mit Docker zu arbeiten, besteht darin, den gesamten Build in ein Docker-Image zu packen.
Ein beliebtes Projekt in diesem Bereich ist Source-to-Image von RedHat.
Aber ist dies der gewünschte Weg, um die Probleme von oben anzugehen? So ist es unwahrscheinlich dass Entwickler
dieses Modell für die lokale Entwicklung übernehmen. Es fehlt die Unterstützung von Gradle als auch von IDEs
wie IntellijJ und Eclipse. Dann drängt sich auch die Frage auf ob es überhaupt bzw. sogar möglich ist
den ganzen Build in einem Image zu haben? Größere Projekte besehen aus vielen Einzelteilen. Häufig entscheiden
sich Projekte für einen Mono-Repository Ansatz um Einfachheit beim Bau, Versionierung und Testing zu erreichen.
Entsprechen werden alle Teile im gleichen Git Repository gehosted, zusammen gebaut und mittels mehrerer 
Artefakte veröffentlicht. Verschiedene Werkzeuge können hier unterschiedliche Anforderungen an das zugrunde
liegende Betriebssystem haben. Allenfalls kommen mehrere Versionen des gleichen Werkzeugs oder Komponente zum Einsatz,
beispielsweise für die Prüfung der Kompatibilität. Aus diesen Gründen wünscht man sich vielleicht eher
eine *Orchestrierung von Entwickler-spezifischer Docker-Images*. Die verschiedenen Docker Images sollten
nahtlos zusammenarbeiten und den Entwicklern im Arbeitsablauf nicht im Wege sehen. Hilfe bekommt man hier von:

- `cli-base`, `kubectl`, `oc`, `terraform`, `az`, `glcoud` Plugins
   von https://github.com/rmee/gradle-plugins/
- https://www.testcontainers.org/

Beide Projekte verfolgen das gleiche Ziel in unterschiedlichen Situationen. Testcontainers vereinfacht die Verwendung von Docker
Images für Unit Testing, während die Gradle-Plugins die Verwendung von Docker-Images innerhalb von Builds erleichtert. 
Zusammen haben sie das Potential all die gewünschten Ziele von oben zu erreichen. Ein entsprechendes Beispiel wird im 
nächsten Abschnitt vorgestellt.

## Deployment mit Helm in die Google Cloud

Die `kubectl`, `oc`, `terraform`, `az`, `glcoud` Plugins von
https://github.com/rmee/gradle-plugins/ sind so konzipiert, dass sie einen
minimale Gradle-Integrationsschicht über deren nativen Pendants bieten. Es ist ausdrücklich nicht das Ziel
ein neues API in Gradle zu etablieren: ein solches würde kontinuierliche Wartung erfordern und von Entwicklern
das Kennenlernen des APIs  verlangen. Stattdessen bieten die Plugins etwas Ähnliches zum Gradle `Exec` Task, welche
aber zusätzlich die Komplexität von Docker verbergen. Die Plugins können wie folgt eingebunden werden:

```
plugins {
    id "com.github.rmee.kubectl" version "1.0.20190725142159"
    id "com.github.rmee.helm" version "1.0.20190725142159"
    id "com.github.rmee.gcloud" version "1.0.20190725142159"
}
```

Ein Zugriff auf GKE in Google Cloud erfolgt dann mit:

```
gcloud {
    keyFile = file("$projectDir/secrets/gcloud.key")
    region = 'my-region'
    project = 'my-project'
    gke {
        clusterName = 'my-cluster'
    }
    cli {
        imageName = 'google/cloud-sdk'
        version = '224.0.0'
    }
}
gcloudSetProject.dependsOn gcloudActivateServiceAccount
gcloudGetKubernetesCredentials.dependsOn gcloudSetProject
```

Der konfigurierte `gcloud.key` Schlüssel bietet technischen Zugriff auf den Cluster. Für `gcloud`
wird auch explizit die verwendete Version definiert, während für Helm die Defaults zum Tragen kommen.
Um nun Befehle mit `helm` und `kubectl` abzusetzen verwendet man:

```
task deploy() {
    dependsOn gcloudGetKubernetesCredentials, helmPackage, tasks.jib
    doFirst {
        File yamlFile = file("build/helm/crnk-example.yaml")
        String imageId = file("build/jib-image.id").text
        helm.exec({
            commandLine = "helm template --name=crnk --set image.tag=${imageId} ${helmPackageCrnkExample.outputs.files.singleFile} --namespace=default"
            stdoutFile = yamlFile
        })
        kubectl.exec({
            commandLine = "kubectl apply -f=${yamlFile} -n=default"
        })
    }
}
```

Dieses Beispiel zeigt diverse wichtige Eigenschaften:

- Der Gradle-Code deklariert die verwendeten Werkzeuge und deren Versionen. Eine lokale Installation ist nicht erforderlich.
- Die abgesetzten Befehle stimmen 1:1 mit denen überein welche Entwickler auch in der in der Befehlszeile ausführen können.
  Es muss nichts Neues erlernt werden. Es ist einfach, außerhalb von Gradle manuell zu experimentieren wenn man sich neuen Aufgaben und Problemen stellt.
- Die Aktualisierung einer Image-Version ermöglicht den sofortigen Zugriff auf die neusten Funktionen.
  Die Plugins sind minimalistisch und weitgehend unabhängig von den verwendeten Image-Version.
- *Die Plugins schützen das Home-Verzeichnis des Benutzers*! Ein neues Home-Verzeichnis wird 
  im  `build`-Verzeichnis angelegt. Es unterliegt dem gleichen Gradle-Lebenszyklus wie jede andere Projektdatei. Eine `gradlew clean`
  entfernt auch das Home-Verzeichnis. Weitere Befehle werden dieses bei Bedarf wieder aufbauen.
- Die `exec`-Methode ändert absolute Host-Pfade zu entsprechenden Docker-Pfaden.
- Die Plugins sind nicht auf die Verwendung von Docker-Images beschränkt. Sie können, sofern erwünscht, auch existierende
  Binärdateien der Host-Maschine zu Ausführung verwenden.
- Alle Plugins verwenden ein gemeinsames `cli-base` Projekt. Es bietet eine (frühe) Abstraktionsschicht welches
  die schnelle Implementation weiterer solcher Integrationen erlaubt.
- Die `exec`-Methode erlaubt auch den Zugriff auf Resultate. Beispielsweise wird  das evaluierte Helm-Template 
  in das `yamlFile` geschrieben.



## Entwicklern die Kontrolle zurückgeben.

Ähnlich wie bei `jdk-bootstrap` adaptieren die verschiedenen Gradle-Plugins aus dem vorherigen Abschnitt den
Gradle `wrapper` Task. Für jedes angewandte Plugin wird ein kleines Shell-Skript generiert. Diese bieten ein Ersatz für deren
native Gegenstücke. So sieht das generierte `kubectl` beispielsweise wie folgt aus:

```
#!/usr/bin/env bash
WORK_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" >/dev/null && pwd)"
[ -x /bin/cygpath ] && WORK_DIR=$(/bin/cygpath -m "$WORK_DIR")

...

exec docker run -i $TTY_PARAM $USER_PARAM --rm \
    "${HTTP_PROXY_PARAM[@]}" \
    ...
    -e HOME=/workdir/build/home \
    --workdir /workdir/build/home \
    -v $WORK_DIR:/workdir \
    google/cloud-sdk:224.0.0 kubectl "$@"
```

Die Verwendung ist fast identisch zur ursprünglichen Binärdatei. Einschränkungen gibt es aktuell noch
bei Pfad und Port-Mappings. Ein Aufruf sieht wie folgt aus:

```
$ ./kubectl get pods -n=kube-system
NAME                                                         READY   STATUS    RESTARTS   AGE
heapster-v1.6.0-beta.1-869f77bc95-n8h4c                      3/3     Running   0          35d
kube-dns-76dbb796c5-2xhdw                                    4/4     Running   0          35d
kube-dns-76dbb796c5-cr8wl                                    4/4     Running   0          35d
kube-dns-autoscaler-67c97c87fb-nl9fv                         1/1     Running   0          35d
kube-proxy-gke-sb4b-test-europe-west6-pool-1-0087b845-38fc   1/1     Running   0          35d
kube-proxy-gke-sb4b-test-europe-west6-pool-1-0087b845-s21t   1/1     Running   0          35d
```

## Ausblick

Das präsentierte Beispiel zeigt wie man eine Applikation mit Helm und Kubernetes in die Google Cloud 
deployen kann. Damit ist der Setup vollständig in sich geschlossen und reproduzierbar.
Alle Werkzeuge und deren Versionen sind durch das Projekt definiert. Die Plege der lokalen
Installation durch die Entwickler entfällt. Artefakte verlassen nicht die Grenzen des Projektverzeichnisses. 
Und Werkzeuge und Versionen lassen sich beliebig kombinieren, auch in den komplexesten Szenarien.
Weitere Informationen finden Sie auf den Webseiten der jeweiligen Projekte.
