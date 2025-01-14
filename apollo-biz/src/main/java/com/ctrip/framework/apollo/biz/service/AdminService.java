package com.ctrip.framework.apollo.biz.service;

import com.ctrip.framework.apollo.biz.entity.Cluster;
import com.ctrip.framework.apollo.common.entity.App;
import com.ctrip.framework.apollo.core.ConfigConsts;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

@Service
public class AdminService {
  private final static Logger logger = LoggerFactory.getLogger(AdminService.class);

  private final AppService appService;
  private final AppNamespaceService appNamespaceService;
  private final ClusterService clusterService;
  private final NamespaceService namespaceService;

  public AdminService(
      final AppService appService,
      final @Lazy AppNamespaceService appNamespaceService,
      final @Lazy ClusterService clusterService,
      final @Lazy NamespaceService namespaceService) {
    this.appService = appService;
    this.appNamespaceService = appNamespaceService;
    this.clusterService = clusterService;
    this.namespaceService = namespaceService;
  }

  /**
   * 创建 App 时， 异步调用同步
   *
   * @param app
   * @return
   */
  @Transactional
  public App createNewApp(App app) {

    // 保存到数据库， 并进行审核
    String createBy = app.getDataChangeCreatedBy();
    App createdApp = appService.save(app);

    String appId = createdApp.getAppId();

    // 创建默认的Namespace
    appNamespaceService.createDefaultAppNamespace(appId, createBy);

    // 创建默认的集群
    clusterService.createDefaultCluster(appId, createBy);

    // 创建 Cluster 默认的命名空间
    namespaceService.instanceOfAppNamespaces(appId, ConfigConsts.CLUSTER_NAME_DEFAULT, createBy);

    return app;
  }

  @Transactional
  public void deleteApp(App app, String operator) {
    String appId = app.getAppId();

    logger.info("{} is deleting App:{}", operator, appId);

    List<Cluster> managedClusters = clusterService.findClusters(appId);

    // 1. delete clusters
    if (Objects.nonNull(managedClusters)) {
      for (Cluster cluster : managedClusters) {
        clusterService.delete(cluster.getId(), operator);
      }
    }

    // 2. delete appNamespace
    appNamespaceService.batchDelete(appId, operator);

    // 3. delete app
    appService.delete(app.getId(), operator);
  }
}
