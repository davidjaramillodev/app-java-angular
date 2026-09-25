import { bootstrapApplication } from '@angular/platform-browser';
import { appConfig } from './app/app.config';
import { LoanPortalComponent } from './app/views/loan-portal.component';

bootstrapApplication(LoanPortalComponent, appConfig).catch((error: unknown) => console.error(error));