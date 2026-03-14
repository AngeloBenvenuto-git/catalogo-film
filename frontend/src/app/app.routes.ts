import { Routes } from '@angular/router';
import { FilmList } from './components/film-list/film-list';
import { FilmDetail } from './components/film-detail/film-detail';
import { Login } from './components/login/login';
import { Registrazione } from './components/registrazione/registrazione';
import { Admin } from './components/admin/admin';

export const routes: Routes = [
  { path: '', component: FilmList },
  { path: 'films/:id', component: FilmDetail },
  { path: 'login', component: Login },
  { path: 'registrazione', component: Registrazione},
  { path: 'admin', component: Admin }
];
