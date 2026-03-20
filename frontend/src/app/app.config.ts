import { ApplicationConfig, provideBrowserGlobalErrorListeners } from '@angular/core';
import { provideRouter, withInMemoryScrolling } from '@angular/router'; // <--- AGGIUNTO withInMemoryScrolling
import { provideHttpClient } from '@angular/common/http';
import { routes } from './app.routes';

export const appConfig: ApplicationConfig = {
  providers: [
    provideBrowserGlobalErrorListeners(),
    // Abbiamo aggiunto l'opzione di configurazione dentro provideRouter
    provideRouter(
      routes,
      withInMemoryScrolling({
        scrollPositionRestoration: 'enabled', // Ripristina la posizione dello scroll
        anchorScrolling: 'enabled'            // Utile se usi le ancore (#)
      })
    ),
    provideHttpClient()
  ]
};
