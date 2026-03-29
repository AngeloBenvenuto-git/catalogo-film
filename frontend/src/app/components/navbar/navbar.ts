import { Component, OnInit, HostListener, ElementRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink, RouterLinkActive, Router } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { AuthService } from '../../services/auth.service';

@Component({
  selector: 'app-navbar',
  standalone: true,
  imports: [CommonModule, RouterLink, RouterLinkActive, FormsModule],
  templateUrl: './navbar.html',
  styleUrl: './navbar.css',
})
export class Navbar implements OnInit {
  isLoggato: boolean = false;
  isScrolled: boolean = false;
  showSearch: boolean = false;
  mobileMenuOpen: boolean = false;
  searchQuery: string = '';
  genereSelezionato: string = '';
  ordinamento: string = '';
  ordinamentoAnno: string = '';

  avatarUrl: string | null = null;
  usernameCorrente: string | null = null;

  constructor(
    public authService: AuthService,
    private router: Router,
    private eRef: ElementRef
  ) {}

  ngOnInit() {
    // 1. ASCOLTA I CAMBIAMENTI DELLA FOTO IN TEMPO REALE!
    this.authService.avatar$.subscribe(avatar => {
      this.avatarUrl = avatar;
    });

    // 2. GESTISCI IL LOGIN E LA SINCRONIZZAZIONE
    this.authService.loggedIn$.subscribe(stato => {
      this.isLoggato = stato;

      if (stato) {
        const email = this.authService.getEmail();

        if (email) {
          // Carica prima dalla cache per immediatezza
          const localAvatar = localStorage.getItem('user_avatar_' + email);
          if (localAvatar) {
            this.authService.setAvatar(localAvatar);
          }
          this.usernameCorrente = localStorage.getItem('custom_username_' + email) || this.authService.getUsername();
        }

        // Recupera i dati freschi dal Server
        if (typeof this.authService.getMe === 'function') {
          this.authService.getMe().subscribe({
            next: (user) => {
              if (user.fotoBase64) {
                localStorage.setItem('user_avatar_' + user.email, user.fotoBase64);
                this.authService.setAvatar(user.fotoBase64); // Aggiorna via radio!
              }
              if (user.username) {
                localStorage.setItem('custom_username_' + user.email, user.username);
                this.usernameCorrente = user.username;
              }
            },
            error: (err) => console.log('Sync profilo saltata:', err)
          });
        }
      } else {
        this.authService.setAvatar(null);
        this.usernameCorrente = null;
      }
    });
  }

  @HostListener('window:scroll', [])
  onWindowScroll() {
    this.isScrolled = window.scrollY > 50;
  }

  @HostListener('document:click', ['$event'])
  clickout(event: any) {
    const searchElement = this.eRef.nativeElement.querySelector('.search-container');
    if (this.showSearch && searchElement && !searchElement.contains(event.target)) {
      this.showSearch = false;
    }
  }

  toggleSearch() {
    this.showSearch = !this.showSearch;
    if (this.showSearch) {
      setTimeout(() => (this.eRef.nativeElement.querySelector('.search-input') as HTMLElement)?.focus(), 100);
    }
  }

  private applicaTutto() {
    this.router.navigate(['/'], {
      queryParams: {
        q: this.searchQuery || null,
        g: this.genereSelezionato || null,
        ord: this.ordinamento || null,
        ordAnno: this.ordinamentoAnno || null
      },
      queryParamsHandling: 'merge'
    });
  }

  eseguiRicerca() {
    this.genereSelezionato = '';
    this.applicaTutto();
  }

  toggleMobileMenu() {
    this.mobileMenuOpen = !this.mobileMenuOpen;
  }

  closeMobileMenu() {
    this.mobileMenuOpen = false;
  }

  setGenere(genere: string) {
    this.genereSelezionato = genere;
    this.searchQuery = '';
    this.applicaTutto();
  }

  setOrdinamento(tipo: string) {
    this.ordinamento = tipo;
    this.ordinamentoAnno = '';
    this.applicaTutto();
  }

  setOrdinamentoAnno(tipo: string) {
    this.ordinamentoAnno = tipo;
    this.ordinamento = '';
    this.applicaTutto();
  }

  resetFiltri() {
    this.searchQuery = '';
    this.genereSelezionato = '';
    this.ordinamento = '';
    this.ordinamentoAnno = '';
    this.router.navigate(['/'], { queryParams: {}, queryParamsHandling: '' });
  }

  getEmail(): string | null { return this.authService.getEmail(); }
  getRuolo(): string | null { return this.authService.getRuolo(); }

  getUsername(): string | null {
    return this.usernameCorrente;
  }

  getAvatar(): string | null {
    return this.avatarUrl;
  }

  logout() {
    this.authService.logout();
    this.router.navigate(['/login']);
  }

  goToFavorites() {
    this.router.navigate(['/favorites']);
  }

  refreshHome() {
    this.resetFiltri();
    this.router.navigate(['/']);
  }
}
