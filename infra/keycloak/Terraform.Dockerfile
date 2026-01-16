FROM hashicorp/terraform:latest

WORKDIR app

COPY main.tf .
COPY outputs.tf .
COPY terraform.tfvars .
COPY variables.tf .

COPY script.sh .

RUN chmod +x script.sh

#ENV TF_LOG=TRACE

ENTRYPOINT ["sh", "script.sh"]
