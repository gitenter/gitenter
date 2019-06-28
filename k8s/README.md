# Kubernetes Notes

## Pre

```
minikube start
minikube dashboard
```

Cleanup (optional for both):

```
minikube stop
minikube delete
```

## Non-persistent storage

```
kubectl apply -f k8s/redis-master-deployment.yaml
kubectl apply -f k8s/redis-master-service.yaml
kubectl apply -f k8s/redis-slave-deployment.yaml
kubectl apply -f k8s/redis-slave-service.yaml
```

To check (separate windows):

```
kubectl port-forward deployment/redis-master 7000:6379
redis-cli -p 7000
```

Clean up:

```
kubectl delete deployment -l app=gitenter
kubectl delete service -l app=gitenter
```

https://kubernetes.io/docs/tutorials/stateless-application/guestbook/
https://kubernetes.io/docs/tasks/access-application-cluster/port-forward-access-application-cluster/#creating-redis-deployment-and-service
