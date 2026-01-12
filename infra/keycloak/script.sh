#!/bin/sh
set -e
max_attempts=10
attempt=1

until terraform init -input=false -upgrade && terraform plan -input=false && terraform apply -auto-approve; do
  echo "Terraform falhou (tentativa $attempt de $max_attempts)"
  if [ "$attempt" -ge "$max_attempts" ]; then
    echo "Erro: atingido número máximo de tentativas."
    exit 1
  fi
  attempt=$((attempt+1))
  sleep 10
done

echo "Terraform aplicado com sucesso!"