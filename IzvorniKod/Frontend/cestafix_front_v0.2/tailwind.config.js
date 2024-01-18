/** @type {import('tailwindcss').Config} */
module.exports = {
  content: [
    "./src/**/*.{js,jsx,ts,tsx}",
  ],
  theme: {},
  plugins: [],
  purge: {
      options: {
          safelist: ['button','submit','input'],
      },
  },
}
