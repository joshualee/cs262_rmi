package securityManagers;

import java.security.Permission;

/**
 * Created by perry on 3/13/14.
 */
public class DumbSecurityManager extends SecurityManager {
  @Override
  public void checkPermission(Permission perm) {
    return;
  }
}
