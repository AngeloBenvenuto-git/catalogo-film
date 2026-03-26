import { Routes } from '@angular/router';
import { FilmList } from './components/film-list/film-list';
import { FilmDetail } from './components/film-detail/film-detail';
import { Login } from './components/login/login';
import { Registrazione } from './components/registrazione/registrazione';
import { Admin } from './components/admin/admin';
import { provideRouter, withInMemoryScrolling } from '@angular/router';
import { FavoritesComponent } from './components/Favorite/favorites.component';
import { ProfileComponent } from './components/profilo/profilo';
import { ListaCurataComponent } from './components/lista-curata/lista-curata.component';
import { CreaListaComponent } from './components/crea-lista/crea-lista.component';


export const routes: Routes = [
  { path: '', component: FilmList },
  { path: 'films/:id', component: FilmDetail },
  { path: 'login', component: Login },
  { path: 'registrazione', component: Registrazione},
  { path: 'admin', component: Admin },
  { path: 'favorites', component: FavoritesComponent },
  { path: 'profile', component: ProfileComponent },
  { path: 'liste', component: ListaCurataComponent },
  { path: 'crea-lista', component: CreaListaComponent }
];
