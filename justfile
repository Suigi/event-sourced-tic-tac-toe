gcloud-registry := "europe-west4-docker.pkg.dev/es-tic-tac-toe/tic-tac-toe/tic-tac-toe"
image-tag := `date +%s`

test:
  ./mvnw test

gcloud-build-image:
  ./mvnw spring-boot:build-image \
    -Dspring.profiles.active=default \
    -Dimage.platform=linux/amd64 \
    -Dimage.name={{gcloud-registry}}:{{image-tag}} \
    -Dimage.publish=false

gcloud-push-image: gcloud-build-image
  docker push {{gcloud-registry}}:{{image-tag}}

gcloud-deploy: gcloud-push-image
  echo Deploying image {{gcloud-registry}}:{{image-tag}}
  gcloud run services update tic-tac-toe-service \
    --region=europe-west4 \
    --image={{gcloud-registry}}:{{image-tag}}
