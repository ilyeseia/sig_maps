<script>
import { Line } from 'vue-chartjs'

export default {
  extends: Line,
  props: ['data', 'name', 'time'],
  mounted() {
    this.renderChart(
      {
        labels:
          this.time === 'Mon'
            ? this.data.map((d) => this.getMonth(d.time))
            : this.data.map(
                (d) => parseInt(d.time[0]) - 1 + new Date().getFullYear()
              ),
        datasets: [
          {
            label: this.name,
            data: this.data.map((d) => d.total),
            backgroundColor: 'transparent',
            borderColor: 'rgb(0, 150, 136)',
            pointBackgroundColor: 'rgb(251, 140, 0)',
          },
        ],
      },
      {
        responsive: true,
        maintainAspectRatio: false,
        title: {
          display: true,
          text: `Nombre des ${this.name.toLowerCase()}(s) par ${
            this.time === 'Mon' ? 'moi' : 'année'
          }`,
        },
      }
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