

const boldIfNotSection = (code) => {
    let num = parseInt(code);
    if (isNaN(num)) {
        throw new Error("param should be a number");
    }
    else if (num % 100 !== 0) {
        return code;
    }
    else {
        return (
          <div style={{fontWeight: "bold"}}>{code}</div>
        )
    }
}

export {
    boldIfNotSection
};
