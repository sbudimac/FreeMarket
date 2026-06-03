{{/*
Expand the chart name.
*/}}
{{- define "freemarket.name" -}}
{{- default .Chart.Name .Values.nameOverride | trunc 63 | trimSuffix "-" }}
{{- end }}

{{/*
Fully-qualified release name (release + chart, deduplicated).
Truncated at 63 characters for DNS compatibility.
*/}}
{{- define "freemarket.fullname" -}}
{{- if .Values.fullnameOverride }}
{{- .Values.fullnameOverride | trunc 63 | trimSuffix "-" }}
{{- else }}
{{- $name := default .Chart.Name .Values.nameOverride }}
{{- if contains $name .Release.Name }}
{{- .Release.Name | trunc 63 | trimSuffix "-" }}
{{- else }}
{{- printf "%s-%s" .Release.Name $name | trunc 63 | trimSuffix "-" }}
{{- end }}
{{- end }}
{{- end }}

{{/*
PostgreSQL StatefulSet and Service name.
*/}}
{{- define "freemarket.postgresql.fullname" -}}
{{- printf "%s-postgresql" (include "freemarket.fullname" .) | trunc 63 | trimSuffix "-" }}
{{- end }}

{{/*
Resolve the PostgreSQL host.
- When postgresql.enabled=true, points at the in-cluster StatefulSet Service.
- Otherwise, uses config.postgresHost (must be explicitly set).
*/}}
{{- define "freemarket.postgresql.host" -}}
{{- if .Values.postgresql.enabled }}
{{- include "freemarket.postgresql.fullname" . }}
{{- else }}
{{- required "config.postgresHost must be set when postgresql.enabled=false" .Values.config.postgresHost }}
{{- end }}
{{- end }}

{{/*
Common labels applied to every resource.
*/}}
{{- define "freemarket.labels" -}}
helm.sh/chart: {{ printf "%s-%s" .Chart.Name .Chart.Version | replace "+" "_" | trunc 63 | trimSuffix "-" }}
{{ include "freemarket.selectorLabels" . }}
{{- if .Chart.AppVersion }}
app.kubernetes.io/version: {{ .Chart.AppVersion | quote }}
{{- end }}
app.kubernetes.io/managed-by: {{ .Release.Service }}
{{- end }}

{{/*
Selector labels for the backend Deployment / Service.
*/}}
{{- define "freemarket.selectorLabels" -}}
app.kubernetes.io/name: {{ include "freemarket.name" . }}
app.kubernetes.io/instance: {{ .Release.Name }}
{{- end }}

{{/*
Selector labels for the PostgreSQL StatefulSet / Service.
*/}}
{{- define "freemarket.postgresql.selectorLabels" -}}
app.kubernetes.io/name: {{ include "freemarket.name" . }}-postgresql
app.kubernetes.io/instance: {{ .Release.Name }}
{{- end }}
