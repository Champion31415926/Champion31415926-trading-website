<template>
  <div class="login-container">
    <div class="left-section">
      <img src="/Vector 1.png" class="bg-line line-top" alt="bg-line">
      <img src="/Vector 2.png" class="bg-line line-bottom" alt="bg-line">

      <div class="content-wrapper">
        <div class="image-gallery">
          <div class="photo-wrapper img-1">
            <img src="/unsplash 1.png" alt="Camera">
          </div>
          <div class="photo-wrapper img-2">
            <img src="/unsplash 2.png" alt="Photographer">
          </div>
          <div class="photo-wrapper img-3">
            <img src="/unsplash 3.png" alt="Lens">
          </div>
        </div>

        <div class="main-icon-wrapper">
            <img src="/mainIcon.png" class="main-icon" alt="Main Icon">
        </div>

        <div class="slogan">
          CAPTURE MOMENTS,<br>
          TRADE SMART
        </div>
      </div>
    </div>

    <div class="right-section">
      <div class="form-wrapper">
        <h1 class="title">Login</h1>
        <p class="subtitle">Welcome back</p>

        <div class="form-body">
          <div class="input-group">
            <input 
              v-model="act" 
              class="styled-input" 
              placeholder="your account" 
              type="text"
              @keyup.enter="login"
            >
          </div>

          <div class="input-group">
            <input 
              v-model="pwd" 
              class="styled-input" 
              placeholder="password" 
              type="password"
              @keyup.enter="login"
            >
          </div>

          <button class="login-btn" @click="login">
            Sign in
          </button>

          <div class="footer-links">
            <span class="create-account" @click="toDoRegister">Create account</span>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
// JS logic remains unchanged
const DELAY_TIME = 1300;
import request from "@/utils/request.js";
import { setToken } from "@/utils/storage.js";
import md5 from 'js-md5';

export default {
  name: "Login",
  data() {
    return {
      act: '',
      pwd: ''
    }
  },
  methods: {
    toDoRegister() {
      this.$router.push('/register');
    },
    async login() {
      if (!this.act || !this.pwd) {
        this.$swal.fire({
          title: 'Input Validation',
          text: 'Account or password cannot be empty',
          icon: 'error',
          showConfirmButton: false,
          timer: DELAY_TIME,
        });
        return;
      }
      const hashedPwd = md5(md5(this.pwd));
      const paramDTO = { userAccount: this.act, userPwd: hashedPwd };
      try {
        const { data } = await request.post(`user/login`, paramDTO);
        if (data.code !== 200) {
          this.$swal.fire({
            title: 'Login Failed',
            text: data.msg,
            icon: 'error',
            showConfirmButton: false,
            timer: DELAY_TIME,
          });
          return;
        }
        setToken(data.data.token);
        setTimeout(() => {
          const { role } = data.data;
          sessionStorage.setItem('role', role);
          this.navigateToRole(role);
        }, DELAY_TIME);
      } catch (error) {
        console.error('Login request error:', error);
        if (this.$message) {
            this.$message.error('Login request failed, please try again!');
        } else {
            alert('Login request failed, please try again!');
        }
      }
    },
    navigateToRole(role) {
      switch (role) {
        case 1:
          this.$router.push('/admin');
          break;
        case 2:
          this.$router.push('/user');
          break;
        default:
          console.warn('Unknown role type:', role);
          break;
      }
    },
  }
};
</script>

<style lang="scss" scoped>
@import url('https://fonts.googleapis.com/css2?family=Poppins:wght@300;400;500&family=Russo+One&display=swap');

$primary-blue: #94a9ff; 
$text-grey: #666;
$input-border: #ccc;

.login-container {
  display: flex;
  width: 100vw;
  height: 100vh;
  overflow: hidden;
  font-family: 'Poppins', sans-serif;
  background: white;
}

/* Left Section */
.left-section {
  flex: 1;
  /* Background color adjusted to #9EAEFF */
  background-color: #9EAEFF; 
  
  position: relative;
  display: flex;
  flex-direction: column;
  justify-content: center; 
  align-items: center;
  overflow: hidden;

  @media (max-width: 768px) {
    display: none;
  }
}

/* Background Line Image Position */
.bg-line {
  position: absolute;
  pointer-events: none;
  z-index: 0; /* Ensure background is at the bottom */
  opacity: 0.6;
  width: 100%; 
  height: auto; 
}

/* --- Curve Position --- */
.line-top {
  top: 30px; 
  left: 80px; 
  transform: translateY(10%) translateX(-10%); 
  width: 100%; 
  max-width: none; 
}

.line-bottom {
  bottom: 10px; 
  right: 90px; 
  transform: translateY(0%) translateX(10%); 
  width: 100%; 
  max-width: none;
}


.content-wrapper {
  position: relative;
  z-index: 1; /* Content container above background */
  width: 100%;
  height: 100%;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center; 
  padding-top: 0; 
}

.image-gallery {
  position: relative;
  width: 100%;
  height: 350px; 
  margin-top: 20px; 
}

/* --- Image Styles --- */
.photo-wrapper { 
  position: absolute;
  
  background: none !important; 
  padding: 0 !important;      
  border: none !important;     
  box-shadow: none !important; 
  
  z-index: 99; 
  
  img {
    display: block;
    width: 100%;
    height: 100%;
    object-fit: cover;
    border-radius: 4px; 
    box-shadow: none; 
  }
}

/* Image Position, Size, and Tilt */
.img-1 { 
  width: 170px; 
  height: 220px; 
  left: 8%; 
  top: 20%; 
  transform: rotate(-3deg); 
}

.img-2 { 
  width: 170px; 
  height: 220px; 
  left: 48%; 
  top: -2%; 
  transform: translateX(-50%) rotate(2deg); 
}

.img-3 { 
  width: 170px; 
  height: 220px; 
  right: 13%; 
  top: 30%; 
  /* **** Key Modification: Tilt in the same direction as img-1 (counterclockwise) **** */
  transform: rotate(-3deg); 
}

/* Main Icon and Slogan */
.main-icon-wrapper {
  margin-top: -100px; 
  /* Use negative margin to bring text closer/slightly overlap main icon */
  margin-bottom: -10px;
  display: flex;
  justify-content: center;
}

.main-icon {
  width: 350px; 
  height: auto;
  object-fit: contain;
  opacity: 0.8;
}

.slogan {
  margin-top: auto;
  /* Maintain distance from bottom */
  margin-bottom: 100px; 
  color: white;
  font-size: 2.5rem;
  font-weight: 500;
  text-align: center;
  line-height: 1.3;
  letter-spacing: 1px;
}

/* Right Section Styles (Unchanged) */
.right-section {
  flex: 1;
  background-color: white;
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  padding: 40px;
}

.form-wrapper {
  width: 100%;
  max-width: 380px;
  text-align: center;
}

.title {
  font-family: 'Russo One', sans-serif;
  font-size: 2.5rem;
  color: $primary-blue;
  margin-bottom: 10px;
  letter-spacing: 1px;
  font-style: italic;
}

.subtitle {
  color: $text-grey;
  margin-bottom: 50px;
  font-size: 0.95rem;
  font-weight: 400;
}

.form-body {
  width: 100%;
}

.input-group {
  margin-bottom: 20px;
  text-align: left;
}

.styled-input {
  width: 100%;
  padding: 16px 20px;
  border: 1.5px solid ;
  $input-border:#000;
  border-radius: 4px;
  font-size: 0.95rem;
  outline: none;
  font-family: 'Poppins', sans-serif;
  color: #333;
  transition: all 0.3s;

  &::placeholder {
    color: #777;
  }

  &:focus {
    border-color: $primary-blue;
    box-shadow: 0 0 0 3px rgba(148, 169, 255, 0.1);
  }
}

.login-btn {
  width: 100%;
  padding: 16px;
  background-color: $primary-blue;
  color: white;
  border: none;
  border-radius: 6px;
  font-family: 'Russo One', sans-serif;
  font-size: 1.2rem;
  font-style: italic; 
  letter-spacing: 2px;
  cursor: pointer;
  margin-top: 20px;
  margin-bottom: 40px;
  transition: background-color 0.3s, transform 0.1s;
  box-shadow: 0 5px 15px rgba(148, 169, 255, 0.4);

  &:hover {
    background-color: darken($primary-blue, 5%);
  }
  
  &:active {
    transform: translateY(2px);
  }
}

.footer-links {
  text-align: center;
}

.create-account {
  color: $text-grey;
  text-decoration: underline;
  font-size: 0.9rem;
  cursor: pointer;
  transition: color 0.3s;

  &:hover {
    color: #000;
  }
}
</style>