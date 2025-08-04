import {
  AutoRefreshTokenService,
  createInterceptorCondition,
  INCLUDE_BEARER_TOKEN_INTERCEPTOR_CONFIG,
  IncludeBearerTokenCondition,
  provideKeycloak,
  UserActivityService,
  withAutoRefreshToken,
} from 'keycloak-angular';
import { environment } from '../environments/environment';

export const provideKeycloakAngular = () =>
  provideKeycloak({
    config: environment.keycloakConfig,
    initOptions: {
      onLoad: 'login-required',
      checkLoginIframe: true,
    },
    features: [
      withAutoRefreshToken({
        onInactivityTimeout: 'logout',
        sessionTimeout: 1000,
      }),
    ],
    providers: [
      AutoRefreshTokenService,
      UserActivityService,
      {
        provide: INCLUDE_BEARER_TOKEN_INTERCEPTOR_CONFIG,
        useValue: [
          createInterceptorCondition<IncludeBearerTokenCondition>({
            urlPattern: /^https?:\/\/.+/i,
          }),
        ],
      },
    ],
  });
