import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { CommonModule, DatePipe } from '@angular/common';
import { MessaggioService } from '../../services/messaggio.service';

@Component({
  selector: 'app-miei-messaggi',
  standalone: true,
  imports: [CommonModule],
  providers: [DatePipe],
  templateUrl: './miei-messaggi.component.html',
  styleUrls: ['./miei-messaggi.component.css']
})
export class MieiMessaggiComponent implements OnInit {
  messaggi: any[] = [];

  constructor(
    private messaggioService: MessaggioService,
    private cdr: ChangeDetectorRef
  ) {}

  ngOnInit() {
    this.caricaMieiMessaggi();
  }

  caricaMieiMessaggi() {
    this.messaggioService.getMieiMessaggi().subscribe({
      next: (data) => {
        this.messaggi = data;
        this.cdr.detectChanges();
      },
      error: (err) => {
        console.error(err);
        this.cdr.detectChanges();
      }
    });
  }
}
