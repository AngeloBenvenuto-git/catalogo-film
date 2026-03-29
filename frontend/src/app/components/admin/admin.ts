import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { AdminService } from '../../services/admin.service';
import { AuthService } from '../../services/auth.service';
import { Router } from '@angular/router';

@Component({
  selector: 'app-admin',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './admin.html',
  styleUrl: './admin.css'
})
export class Admin implements OnInit {
  utenti: any[] = [];
  errore: string = '';
  ricerca: string = '';

  constructor(
    private adminService: AdminService,
    private authService: AuthService,
    private router: Router,
    private cdr: ChangeDetectorRef
  ) {}

  ngOnInit() {
    if (this.authService.getRuolo() !== 'ADMIN') {
      this.router.navigate(['/']);
      return;
    }
    this.caricaUtenti();
  }

  get utentiFiltrati() {
    return this.utenti.filter(u =>
      u.username.toLowerCase().includes(this.ricerca.toLowerCase()) ||
      u.email.toLowerCase().includes(this.ricerca.toLowerCase())
    );
  }

  caricaUtenti() {
    this.adminService.getTuttiUtenti().subscribe({
      next: (data) => {
        this.utenti = data;
        this.cdr.detectChanges();
      },
      error: () => this.errore = 'Errore nel caricamento utenti'
    });
  }

  banna(id: number) {
    this.adminService.bannaUtente(id).subscribe({
      next: () => {
        this.caricaUtenti();
        this.cdr.detectChanges();
      }
    });
  }

  sbanna(id: number) {
    this.adminService.sbannaUtente(id).subscribe({
      next: () => {
        this.caricaUtenti();
        this.cdr.detectChanges();
      }
    });
  }

  promuovi(id: number) {
    this.adminService.promuoviARedattore(id).subscribe({
      next: () => this.caricaUtenti()
    });
  }

  elimina(id: number) {
    if (confirm('Sei sicuro di voler eliminare questo utente?')) {
      this.adminService.eliminaUtente(id).subscribe({
        next: () => this.caricaUtenti()
      });
    }
  }
}
