import {ComponentFixture, TestBed} from '@angular/core/testing';
import {WelcomeComponent} from '../../app/welcome/components/welcome.component';
import {HttpClientTestingModule} from '@angular/common/http/testing';
import {BrowserAnimationsModule} from '@angular/platform-browser/animations';
import {WelcomeService} from '../../app/welcome/service/welcome.service';


describe('WelcomeComponent', () => {
  let component: WelcomeComponent;
  let fixture: ComponentFixture<WelcomeComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [WelcomeComponent, BrowserAnimationsModule, HttpClientTestingModule],
      providers: [WelcomeService]
    })
      .compileComponents();

    fixture = TestBed.createComponent(WelcomeComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
