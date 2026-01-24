<script>
import { Bar } from 'vue-chartjs'

export default {
  extends: Bar,
  props: ['data', 'time', 'name'],
  mounted() {
    this.renderChart(
      {
        labels:
          this.time === 'Mon'
            ? this.data.map((d) => this.getMonth(d.time))
            : this.time === 'Year'
            ? this.data.map(
                (d) => parseInt(d.time[0]) - 1 + new Date().getFullYear()
              )
            : this.data.map((d) => d.name),
        datasets: [
          {
            label: `Nombre des ${this.name.toLowerCase()}(s) par ${
              this.time === 'Mon'
                ? 'moi'
                : this.time === 'Year'
                ? 'année'
                : 'couche'
            }`,
            backgroundColor: ['#fb8c00', '#36a2eb', '#cc65fe', '#f05945', '#ff6384', '#ffce56', '#536dfe', '#f8a488', '$color-primary', '#7cb342', '#9ddfd3', '#93329e'],
            data: this.data.map((d) =>
              this.time === 'layer' ? d.total - 1 : d.total
            ),
          },
        ],
      },
      { responsive: true, maintainAspectRatio: false }
    )
  },
  methods: {
    getMonth(month) {
      switch (month) {
        case 'Jan':
          return 'Janvier'
        case 'Feb':
          return 'Février'
        case 'Mar':
          return 'Mars'
        case 'Apr':
          return 'Avril'
        case 'May':
          return 'Mai'
        case 'Jun':
          return 'Juin'
        case 'Jul':
          return 'Juillet'
        case 'Aug':
          return 'Août'
        case 'Sep':
          return 'Septembre'
        case 'Oct':
          return 'Octobre'
        case 'Nov':
          return 'Nobembre'
        case 'Dec':
          return 'Décembre'
      }
    },
  },
}
</script>
