import { Component, Input, OnInit, OnDestroy } from '@angular/core';

@Component({
  selector: 'app-countdown',
  standalone: true,
  template: `<span [class.expired]="expired">{{ display }}</span>`,
  styles: [`.expired { color: var(--mat-sys-error); font-weight: 500; }`],
})
export class Countdown implements OnInit, OnDestroy {
  @Input({ required: true }) endTime!: string;

  display = '';
  expired = false;
  private intervalId?: ReturnType<typeof setInterval>;

  ngOnInit(): void {
    this.update();
    this.intervalId = setInterval(() => this.update(), 1000);
  }

  ngOnDestroy(): void {
    if (this.intervalId) clearInterval(this.intervalId);
  }

  private update(): void {
    const diff = new Date(this.endTime).getTime() - Date.now();
    if (diff <= 0) {
      this.display = 'Ended';
      this.expired = true;
      if (this.intervalId) clearInterval(this.intervalId);
      return;
    }
    const days = Math.floor(diff / 86400000);
    const hours = Math.floor((diff % 86400000) / 3600000);
    const mins = Math.floor((diff % 3600000) / 60000);
    const secs = Math.floor((diff % 60000) / 1000);

    this.display = days > 0
      ? `${days}d ${hours}h ${mins}m`
      : `${hours}h ${mins}m ${secs}s`;
  }
}
