import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router, RouterModule } from '@angular/router';
import { ListaCurataService } from '../../services/lista-curata.service';

@Component({
  selector: 'app-crea-lista',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterModule],
  templateUrl: './crea-lista.component.html',
  styleUrls: ['./crea-lista.component.css']
})
export class CreaListaComponent {
  // Modello per il form
  nuovaLista = {
    titolo: '',
    descrizione: ''
  };

  errore: string = '';

  constructor(
    private listaService: ListaCurataService,
    private router: Router
  ) {}

  onSubmit() {
    if (!this.nuovaLista.titolo || !this.nuovaLista.descrizione) {
      this.errore = "Per favore, compila tutti i campi.";
      return;
    }

    this.listaService.creaLista(this.nuovaLista).subscribe({
      next: (res) => {
        // Navighiamo verso la rotta delle liste
        this.router.navigate(['/liste']);
      },
      error: (err) => {
        console.error(err);
        this.errore = "Errore durante la creazione. Riprova.";
      }
    });
  }
}
