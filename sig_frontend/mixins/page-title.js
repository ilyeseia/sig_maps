
export default {
    head() {
        return {
          title: this.page.title + "| KharitaDZ",
          meta: [
            {
              hid: this.page.hid,
              name: this.page.name,
              content: this.page.description
            }
          ]
        }
      }
}