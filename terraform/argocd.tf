resource "null_resource" "kubectl_config" {
  provisioner "local-exec" {
    command = "aws eks --region ${local.region} update-kubeconfig --name ${local.eks_name}"
  }
  depends_on = [module.eks]
}

resource "helm_release" "argo" {
    name = "argocd"
    chart = "argo-cd"
    repository = "https://argoproj.github.io/argo-helm"
    namespace = "argocd"
    create_namespace = true

    values = [file("values/argocd.yaml")]
}
