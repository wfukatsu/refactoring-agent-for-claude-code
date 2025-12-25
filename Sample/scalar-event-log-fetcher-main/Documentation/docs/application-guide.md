# Application Configuration Guide

The application supports two approaches:
1. **Using ScalarDB**
2. **Using ScalarDB Cluster**

---

## Using ScalarDB

### Step 1: No Change in Dependency
Check the following dependency in `build.gradle`:
```groovy
//scalar DL - Scalar DB is included in this dependency
implementation 'com.scalar-labs:scalardl-java-client-sdk:3.10.0'
```

### Step 2: Configure Properties
Update `scalardb.properties` with these settings:
```properties
# Connection Properties for ScalarDB
scalar.db.contact_points=<CASSANDRA_IP>
scalar.db.username=cassandra
scalar.db.password=cassandra
scalar.db.storage=cassandra
```

---

## Using ScalarDB Cluster

### Step 1: Add Dependency
Use the ScalarDB Cluster SDK in `build.gradle`:
```groovy
implementation 'com.scalar-labs:scalardb-cluster-java-client-sdk:3.14.0'
```

### Step 2: Update Properties
Configure `scalardb.properties` for ScalarDB Cluster:
```properties
## Connection Properties for ScalarDB Cluster
scalar.db.transaction_manager=cluster
scalar.db.contact_points=indirect:localhost
scalar.db.contact_port=60053
scalar.db.cluster.auth.enabled=true
scalar.db.sql.enabled=true
scalar.db.username=admin
scalar.db.password=admin
```

---

## Application Setup for ScalarDB Cluster Configuration

### 1. Configure License Key and Certificate
Set these environment variables:
- `SCALAR_DB_CLUSTER_LICENSE_KEY`: Your license key.
- `SCALAR_DB_CLUSTER_LICENSE_CHECK_CERT_PEM`: Certificate for license verification.

### 2. Storage Configuration for Scalar-Log-Fetcher
Add these settings to your Helm values file:
```yaml
# Storage configurations
scalar.db.storage=cassandra
scalar.db.contact_points=localhost
scalar.db.username=cassandra
scalar.db.password=cassandra
```

### 3. Custom ScalarDB Cluster Configuration
- Navigate a directory `K8s-scalardb-cluster`.
- Check and configure Storage Configuration in `scalardb-cluster-custom-values.yaml` for Helm deployment.

### 4. Enable ScalarDB Authentication
Ensure authentication is enabled in your configuration for security.

### 5. Deploy ScalarDB Cluster
Follow the official [ScalarDB Cluster Deployment Guide](https://scalardb.scalar-labs.com/docs/latest/scalardb-cluster/setup-scalardb-cluster-on-kubernetes-by-using-helm-chart#step-2-deploy-scalardb-cluster-on-the-kubernetes-cluster-by-using-a-helm-chart) for Helm-based deployment.

---
