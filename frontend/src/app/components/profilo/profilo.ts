import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { AuthService } from '../../services/auth.service';

@Component({
  selector: 'app-profilo',
  standalone: true,
  imports: [CommonModule, RouterModule, FormsModule],
  templateUrl: './profilo.html',
  styleUrls: ['./profilo.css']
})
export class ProfileComponent implements OnInit {
  username: string = '';
  email: string = '';
  avatarPreview: string | null = null;

  // Campi per la modifica password
  nuovaPassword: string = '';
  confermaPassword: string = '';

  // Variabili per il Toast personalizzato
  showToast: boolean = false;
  toastMessage: string = '';

  constructor(private authService: AuthService) {}

  ngOnInit(): void {
    // Recuperiamo i dati iniziali dal service
    this.username = this.authService.getUsername() || '';
    this.email = this.authService.getEmail() || '';

    // Recupera l'immagine profilo salvata nel browser per l'anteprima
    const savedAvatar = localStorage.getItem('user_avatar');
    if (savedAvatar) {
      this.avatarPreview = savedAvatar;
    }
  }

  onFileSelected(event: any): void {
    const file = event.target.files[0];
    if (file) {
      const reader = new FileReader();
      reader.onload = (e: any) => {
        const base64Image = e.target.result;
        this.avatarPreview = base64Image;
        // Salviamo l'immagine localmente così la Navbar la vede subito al refresh
        localStorage.setItem('user_avatar', base64Image);
      };
      reader.readAsDataURL(file);
    }
  }

  // Funzione per attivare il Toast Netflix style
  triggerToast(message: string) {
    this.toastMessage = message;
    this.showToast = true;
    // Scompare dopo 2.5 secondi
    setTimeout(() => {
      this.showToast = false;
    }, 2500);
  }

  salvaProfilo(): void {
    // 1. Controllo validità Password
    if (this.nuovaPassword !== '' || this.confermaPassword !== '') {
      if (this.nuovaPassword !== this.confermaPassword) {
        this.triggerToast('Le password non coincidono!');
        return;
      }
      if (this.nuovaPassword.length < 4) {
        this.triggerToast('La password deve avere almeno 4 caratteri!');
        return;
      }
    }

    // 2. Aggiornamento locale immediato (per sincronizzare la Navbar subito)
    localStorage.setItem('custom_username', this.username);

    // 3. Chiamata al Backend Java per rendere le modifiche persistenti sul Database
    this.authService.updateProfile(this.username, this.nuovaPassword || undefined).subscribe({
      next: (response) => {
        this.triggerToast('Profilo aggiornato con successo!');
        // Attendiamo che il toast sia visibile prima di ricaricare
        setTimeout(() => {
          window.location.href = '/';
        }, 1500);
      },
      error: (err) => {
        // Fallback se il server non è ancora configurato
        console.warn('Backend non raggiungibile, modifiche salvate solo nel browser.', err);
        this.triggerToast('Modifiche salvate localmente!');
        setTimeout(() => {
          window.location.href = '/';
        }, 1500);
      }
    });
  }
}
