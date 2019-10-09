```
cd ~/Workspace/gitenter/
cd k8s/configmaps
kubectl apply -f aws-auth-cm.yaml
kubectl describe configmap -n kube-system aws-auth
```

```
kubectl get pods --all-namespaces
kubectl -n kube-system describe pods coredns-8455f84f99-4cvct
# will error out the same as deploying a normal pod: no nodes available to schedule pods
```

TODO: Still "worker nodes fail to join cluster"
