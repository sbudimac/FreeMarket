#!/usr/bin/env bash
# Local development helper — builds the backend image inside Minikube and
# installs/upgrades the Helm release.
#
# Usage:
#   ./scripts/minikube-dev.sh          # start + deploy
#   ./scripts/minikube-dev.sh --url    # print the service URL after deploying
set -euo pipefail

RELEASE=freemarket
CHART=./helm/freemarket
VALUES=./helm/freemarket/values-local.yaml
SECRETS=./helm/freemarket/values-local.secrets.yaml
NAMESPACE=default

# ── 1. Start Minikube if it is not already running ───────────────────────────
if ! minikube status --format '{{.Host}}' 2>/dev/null | grep -q "Running"; then
  echo "▶ Starting Minikube (2 CPUs, 3 GiB RAM)…"
  minikube start --cpus=2 --memory=3072
else
  echo "✔ Minikube already running"
fi

# ── 2. Build the Docker image directly into Minikube's Docker daemon ─────────
echo "▶ Building freemarket-backend:latest inside Minikube's Docker daemon…"
eval "$(minikube docker-env)"
docker build -t freemarket-backend:latest ./platform

# ── 3. Install or upgrade the Helm release ───────────────────────────────────
if [[ ! -f "${SECRETS}" ]]; then
  echo "✖ Missing ${SECRETS}"
  echo "  Copy the example and fill in your credentials:"
  echo "    cp helm/freemarket/values-local.secrets.yaml.example ${SECRETS}"
  exit 1
fi

echo "▶ Deploying Helm release '${RELEASE}'…"
helm upgrade --install "${RELEASE}" "${CHART}" \
  --namespace "${NAMESPACE}" \
  -f "${VALUES}" \
  -f "${SECRETS}" \
  --wait \
  --timeout 3m

echo ""
echo "✔ Deployment complete."

# ── 4. Optionally print the service URL ──────────────────────────────────────
if [[ "${1:-}" == "--url" ]]; then
  echo ""
  echo "Service URL:"
  minikube service "${RELEASE}" --url --namespace "${NAMESPACE}"
fi
