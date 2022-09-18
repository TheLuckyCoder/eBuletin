import {
  Card,
  CardContent,
  Grid,
  Paper,
  TableCell,
  TableContainer,
  TableRow,
  Tooltip,
  Typography,
} from "@mui/material";
import React from "react";
import { ITransaction } from "../../types/transaction";
import ReactJson from "react-json-view";
import { Link } from "react-router-dom";

interface Props {
  transactions: ITransaction[] | null;
}

const TransactionCard = ({ transaction }: { transaction: ITransaction }) => {
  return (
    <Card sx={{ mt: 1, mb: 1 }}>
      <CardContent>
        <Grid container spacing={2}>
          <Grid item xs={12}>
            <Tooltip title={transaction.hash}>
              <Typography variant="h6">Hash: {transaction.hash}</Typography>
            </Tooltip>
            <Tooltip title={transaction.sender}>
              <Typography variant="h6">
                Sender:{" "}
                {
                  <Link
                    style={{ color: "#17C6B1" }}
                    to={`${transaction.receiver}`}
                  >
                    {transaction.receiver}
                  </Link>
                }
              </Typography>
            </Tooltip>
            <Tooltip title={transaction.receiver}>
              <Typography variant="h6">
                Receiver:{" "}
                {
                  <Link
                    style={{ color: "#17C6B1" }}
                    to={`${transaction.receiver}`}
                  >
                    {transaction.receiver}
                  </Link>
                }
              </Typography>
            </Tooltip>
          </Grid>
          <Grid item xs={12}>
            <Typography variant="h6">Additional Data:</Typography>
            <ReactJson
              name={false}
              theme="ocean"
              iconStyle="square"
              enableClipboard={false}
              displayDataTypes={false}
              displayObjectSize={false}
              collapsed={3}
              quotesOnKeys={false}
              src={{
                data: transaction.data,
                signature: transaction.signature,
              }}
            />
          </Grid>
        </Grid>
      </CardContent>
    </Card>
  );
};

export const Transactions = ({ transactions }: Props) => {
  console.log(transactions);
  if (transactions === null) return null;

  if (transactions.length === 0) {
    return <Typography mt={2}>No transactions for this block</Typography>;
  }
  return (
    <>
      <Typography mb={2} mt={4} variant="h5">
        Transactions
      </Typography>
      {transactions.map((transaction) => (
        <TransactionCard transaction={transaction} />
      ))}
    </>
  );
};
