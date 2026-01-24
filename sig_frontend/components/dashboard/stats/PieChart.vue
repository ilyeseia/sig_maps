<script>
import { Pie } from 'vue-chartjs'

export default {
  extends: Pie,
  props: ['data'],
  mounted() {
    this.gradient = this.$refs.canvas
      .getContext('2d')
      .createLinearGradient(0, 0, 0, 450)
    this.gradient2 = this.$refs.canvas
      .getContext('2d')
      .createLinearGradient(0, 0, 0, 450)
    this.gradient3 = this.$refs.canvas
      .getContext('2d')
      .createLinearGradient(0, 0, 0, 450)
    this.gradient4 = this.$refs.canvas
      .getContext('2d')
      .createLinearGradient(0, 0, 0, 450)
    this.gradient.addColorStop(0, 'rgb(0, 150, 136, .8)')
    this.gradient.addColorStop(1, 'rgb(0, 150, 136, 5)')

    this.gradient2.addColorStop(0, 'rgba(251, 140, 0, .8)')
    this.gradient2.addColorStop(1, 'rgba(251, 140, 0, .5)')

    this.gradient3.addColorStop(0, 'rgba(128, 128, 128, .8)')
    this.gradient3.addColorStop(1, 'rgb(128, 128, 128, .5)')

    this.gradient4.addColorStop(0, 'rgba(255, 0, 0, .8)')
    this.gradient4.addColorStop(1, 'rgba(255, 0, 0, .5)')
    this.renderChart(
      {
        labels: Object.keys(this.data),
        datasets: [
          {
            backgroundColor: Object.keys(this.data).length === 2 ? [this.gradient, this.gradient4] :   [this.gradient4, this.gradient3, this.gradient2, this.gradient],
            data: Object.values(this.data),
          },
        ],
      },
      { responsive: true, maintainAspectRatio: false }
    )
  },
}
</script>