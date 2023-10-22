# progiProjekt
### Postavke baze podataka (Linux)
Kako bi se aplikacija mogla pokrenut potrebno je imati pokrenutu i ispravno konfiguriranu instancu postgresql servera. Ove naredbe bi radile za Arch linux, ali varijacije na temu bi trebale biti za druge sustave:
```
sudo pacman -S postgresql
```
Instalacija (bar u ovom slučaju) kreira novog postgres korisnika na sustavu. Potrebno je ući u tog korisnika:
```
sudo -iu postgres
```
Baza se inicijalizira s hrvatskim postavkama lokala i UTF8 kodiranjem. Direktorij baze je proizvoljan. Naredba se **ne** pokreće kao root:

```
initdb -D /var/lib/postgres/data --locale=hr_HR.utf8 --encoding=UTF8 --auth-local=trust
```
Nakon toga pokrećemo postgresql server (za systemd sustave). Potrebno je prvo izaći iz postgres korisnika:
```
exit
sudo systemctl start postgresql.service
```
Pokretanje za ostale sustave:
```
pg_ctl -D /var/lib/postgres/data -l logfile start
```

Nakon toga napravimo superuser korisnika `admin` i bazu podataka `prijava_ostecenja_db`:
```
createuser -s admin
createdb prijava_ostecenja_db
```