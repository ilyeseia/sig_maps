<template>
  <footer class="footer">
    <div class="container-fluid">
      <div class="row">
        <div class="col d-flex justify-content-center">
          <a href="https://www.eadn.dz">
            <img class="company-logo" src="../../assets/eadn.png" alt="" width="30" />
          </a>
          <div class="col d-flex justify-content-start">
            <p class="text-center">
              <a href="https://www.eadn.dz">EADN</a> &copy; 2020 | Tous droits réservés
            </p>
          </div>
        </div>
        <div class="col text-center">
          <ul class="list-unstyled d-flex justify-content-end">
            <li class="ml-3 social-link">
              <a href="https://www.facebook.com/mjsalgeria/">
                <i class="fab fa-facebook"></i>
              </a>
            </li>
            <li class="ml-3 social-link">
              <a href="https://www.linkedin.com/company/eadndz/">
                <i class="fab fa-youtube"></i
              ></a>
            </li>
          </ul>
        </div>
      </div>
    </div>
  </footer>
</template>

<style scoped>
.footer {
  padding: 20px 0 0 0;
  width: 100%;
  background: $color-primary;
  color: white;
}
.footer .company-logo {
  background: white;
  padding: 5px;
  margin-top: -5px;
  border-radius: 50px;
}
.footer .social-link {
  background: white;
  padding: 5px;
  width: 30px;
  line-height: 23px;
  height: 30px;
  border-radius: 50px;
}
.footer .social-link a i {
  color: $color-primary;
}

.footer a {
  text-decoration: none;
  color: white;
}
</style>
