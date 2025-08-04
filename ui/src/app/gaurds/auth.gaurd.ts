import { AuthGuardData, createAuthGuard } from 'keycloak-angular';
import {
  ActivatedRouteSnapshot,
  CanActivateFn,
  RouterStateSnapshot,
} from '@angular/router';

const isAccessAllowed = async (
  route: ActivatedRouteSnapshot,
  __: RouterStateSnapshot,
  authData: AuthGuardData,
): Promise<boolean> => {
  const { authenticated } = authData;
  return authenticated;
};

export const canActivate = createAuthGuard<CanActivateFn>(isAccessAllowed);
