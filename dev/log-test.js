
setInterval(() => {
  console.log(JSON.stringify({
    type: 'Error',
    stack: 'Test\nlines\nstack',
    message: 'Test error message',
    service: "test-service"
  }));
}, 5000)
